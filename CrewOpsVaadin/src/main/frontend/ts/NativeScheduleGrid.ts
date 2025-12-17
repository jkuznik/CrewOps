import { html, LitElement } from 'lit';
import { styleMap } from 'lit/directives/style-map.js';

interface Shift {
    id: string;
    name: string;
    color: string;
    startMinute: number;
    duration: number;
    isCross: boolean;
}

interface Day {
    dayNumber: number;
    shifts: Shift[];
}

export class NativeScheduleGrid extends LitElement {

    static properties = {
        days: { type: Array },
        activeDragId: { state: true }
    };

    // Inicjalizacja pustą tablicą zapobiega błędom .map()
    days: Day[] = [];
    private activeDragId: string | null = null;
    private isResizing = false;
    private resizeData?: any;

    createRenderRoot() {
        return this; // Light DOM dla łatwiejszego stylowania
    }

    render() {
        // Logika obronna: jeśli nie ma dni, pokaż chociaż placeholder
        if (!this.days || this.days.length === 0) {
            return html`
                <div class="schedule-container" style="min-height: 200px; border: 1px dashed #ccc; display: flex; align-items: center; justify-content: center;">
                    ${this.renderHeader()}
                    <div style="margin-top: 50px; color: #999;">Brak danych do wyświetlenia (pusta lista days)</div>
                </div>
            `;
        }

        return html`
            <div class="schedule-container">
                ${this.renderHeader()}
                <div class="days-wrapper" style="margin-top: 10px;">
                    ${this.days.map(day => this.renderDay(day))}
                </div>
            </div>
        `;
    }

    private renderHeader() {
        const hours = Array.from({length: 24}, (_, i) => i);
        return html`
            <div class="schedule-header">
                <div class="header-label-spacer"></div>
                <div class="header-timeline">
                    ${hours.map(hour => html`
                        <div class="hour-marker">
                            <span class="hour-text">${hour}:00</span>
                        </div>
                    `)}
                </div>
            </div>
        `;
    }

    private renderDay(day: Day) {
        const rows = this.packShiftsIntoRows(day.shifts || []);
        return html`
            <div class="day-row"
                 @drop=${(e: DragEvent) => this.handleDrop(e, day)}
                 @dragover=${(e: DragEvent) => e.preventDefault()}>
                <div class="day-label">Dzień ${day.dayNumber}</div>
                <div class="day-content">
                    ${rows.map(rowShifts => html`
                        <div class="shift-track">
                            ${rowShifts.map(shift => this.renderShift(shift))}
                        </div>
                    `)}
                </div>
            </div>
        `;
    }

    private renderShift(shift: Shift) {
        const left = (shift.startMinute / 1440) * 100;
        const width = (shift.duration / 1440) * 100;

        const styles = {
            left: `${left}%`,
            width: `${width}%`,
            backgroundColor: shift.color || '#3498db'
        };

        return html`
            <div class="shift-bar ${shift.isCross ? 'is-shadow' : ''}"
                 id="shift-${shift.id}"
                 style=${styleMap(styles as any)}
                 draggable="true"
                 @dragstart=${(e: DragEvent) => this.handleDragStart(e, shift)}>
                <span class="shift-name">${shift.name}</span>
            </div>
        `;
    }

    // --- LOGIKA POZOSTAJE BEZ ZMIAN ---
    private handleDragStart(e: DragEvent, shift: Shift) {
        this.activeDragId = shift.id;
        if (e.dataTransfer) { e.dataTransfer.setData('application/json', JSON.stringify(shift)); }
    }

    private handleDrop(e: DragEvent, day: Day) {
        e.preventDefault();
        let shiftId = '';

        try {
            const data = JSON.parse(e.dataTransfer?.getData('application/json') || '{}');
            shiftId = data.id;
        } catch (err) {
            // Backup dla prostego tekstu
            shiftId = e.dataTransfer?.getData('text') || this.activeDragId || '';
        }

        const container = (e.currentTarget as HTMLElement).querySelector('.day-content') as HTMLElement;
        const rect = container.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const minute = Math.max(0, Math.min(1439, Math.round((x / rect.width) * 1440)));

        if (shiftId) {
            this.dispatchEvent(new CustomEvent('shift-dropped', {
                detail: {
                    shiftId: shiftId,
                    dayNumber: day.dayNumber,
                    newStartMinute: minute
                },
                bubbles: true,
                composed: true
            }));
        }

        this.activeDragId = null;
    }

    private packShiftsIntoRows(shifts: Shift[]): Shift[][] {
        const sorted = shifts.slice().sort((a, b) => a.startMinute - b.startMinute);
        const rows: Shift[][] = [];
        for (const shift of sorted) {
            let placed = false;
            for (const row of rows) {
                const last = row[row.length - 1];
                if (shift.startMinute >= (last.startMinute + last.duration)) {
                    row.push(shift);
                    placed = true;
                    break;
                }
            }
            if (!placed) rows.push([shift]);
        }
        return rows;
    }
}

if (!customElements.get('native-schedule-grid')) {
    customElements.define('native-schedule-grid', NativeScheduleGrid);
}