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

    private resizeData: { shift: Shift, startX: number, startDuration: number, container: HTMLElement } | null = null;

    createRenderRoot() {
        return this; // Light DOM dla łatwiejszego stylowania
    }

    render() {
        if (!this.days || this.days.length === 0) {
            return html`
                <div class="schedule-container" style="min-height: 200px; border: 1px dashed #ccc; display: flex; align-items: center; justify-content: center;">
                    ${this.renderHeader()}
                    <div style="margin-top: 50px; color: #999;">Brak danych do wyświetlenia</div>
                </div>
            `;
        }

        return html`
            <div id="schedule-tooltip" class="schedule-tooltip"></div>
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
                 @drop=${(e: DragEvent) => { this.hideTooltip(); this.handleDrop(e, day); }}
            // Wewnątrz metody renderDay, w obsłudze @dragover:
            @dragover=${(e: DragEvent) => {
                e.preventDefault();
                const container = (e.currentTarget as HTMLElement).querySelector('.day-content') as HTMLElement;
                if (!container) return;

                const rect = container.getBoundingClientRect();
                const x = e.clientX - rect.left;
                
                const rawMinute = (x / rect.width) * 1440;
                let minute = this.snapTo15Minutes(rawMinute);
                
                if (minute < 0) {
                    minute = 0;
                }
                
                if (minute >= 1440) {
                    minute = 1425;
                }
                
                const h = Math.floor(minute / 60).toString().padStart(2, '0');
                const m = (minute % 60).toString().padStart(2, '0');

                this.updateTooltip(e, `Start: ${h}:${m}`);
            }}
                 @dragleave=${() => this.hideTooltip()}>
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

        const endsAtMidnight = (shift.startMinute + shift.duration) >= 1440;

        const styles = {
            left: `${left}%`,
            width: `${width}%`,
            backgroundColor: shift.color || '#3498db'
        };

        return html`
            <div class="shift-bar ${shift.isCross ? 'is-shadow' : ''} ${endsAtMidnight ? 'ends-at-midnight' : ''}"
                 id="shift-${shift.id}-${shift.isCross ? 'shadow' : 'main'}"
                 style=${styleMap(styles as any)}
                 draggable="true"
                 @dragstart=${(e: DragEvent) => this.handleDragStart(e, shift)}>
            
            <span class="shift-name" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                ${shift.name}
            </span>

                <div class="resize-handle"
                     @mousedown=${(e: MouseEvent) => this.initResize(e, shift)}>
                </div>
            </div>
        `;
    }

    private initResize(e: MouseEvent, shift: Shift) {
        e.preventDefault();
        e.stopPropagation();

        const container = (e.currentTarget as HTMLElement).closest('.day-content') as HTMLElement;
        if (!container) return;

        this.resizeData = {
            shift,
            startX: e.clientX,
            startDuration: shift.duration,
            container
        };

        // Dodajemy klasy wizualne
        const barId = `shift-${shift.id}-${shift.isCross ? 'shadow' : 'main'}`;
        const bar = document.getElementById(barId);
        bar?.classList.add('resizing');

        // Rejestrujemy globalne zdarzenia ruchu i upuszczenia
        window.addEventListener('mousemove', this.doResize);
        window.addEventListener('mouseup', this.stopResize);
    }

    private doResize = (e: MouseEvent) => {
        if (!this.resizeData) return;

        const { shift, startX, startDuration, container } = this.resizeData;
        const rect = container.getBoundingClientRect();
        const deltaX = e.clientX - startX;
        const deltaMinutesSnapped = this.snapTo15Minutes((deltaX / rect.width) * 1440);

        let newDuration = Math.max(15, startDuration + deltaMinutesSnapped);

        // TOOLTIP: Pokazujemy czas trwania
        this.updateTooltip(e, `Czas: ${this.formatMinutesToTime(newDuration)}`);

        // Szukamy obu segmentów (main i shadow)
        const mainBar = document.getElementById(`shift-${shift.id}-main`);
        const shadowBar = document.getElementById(`shift-${shift.id}-shadow`);

        if (shift.isCross) {
            // Rozciągamy CIEŃ -> Aktualizujemy wizualnie tylko cień
            if (shadowBar) shadowBar.style.width = `${(newDuration / 1440) * 100}%`;
        } else {
            // Rozciągamy ORYGINAŁ
            const endMinute = shift.startMinute + newDuration;

            if (endMinute > 1440) {
                // Przekracza północ wizualnie
                if (mainBar) mainBar.style.width = `${((1440 - shift.startMinute) / 1440) * 100}%`;

                // Jeśli istnieje element cienia w DOM (już został stworzony przez serwer wcześniej)
                if (shadowBar) {
                    const shadowDuration = endMinute - 1440;
                    shadowBar.style.width = `${(shadowDuration / 1440) * 100}%`;
                }
            } else {
                // Nie przekracza północy
                if (mainBar) mainBar.style.width = `${(newDuration / 1440) * 100}%`;
                if (shadowBar) shadowBar.style.width = `0%`;
            }
        }
        this.resizeData.shift.duration = newDuration;
    }

    private stopResize = () => {
        if (!this.resizeData) return;
        this.hideTooltip();

        const { shift } = this.resizeData;

        // Czyścimy UI
        const bar = document.getElementById(`shift-${shift.id}`);
        bar?.classList.remove('resizing');

        // Wysyłamy event do Javy
        this.dispatchEvent(new CustomEvent('shift-resized', {
            detail: {
                shiftId: shift.id,
                newStartMinute: shift.isCross ? -1 : shift.startMinute,
                newDurationMinutes: shift.duration,
                isShadow: shift.isCross // Nowe pole!
            },
            bubbles: true,
            composed: true
        }));

        window.removeEventListener('mousemove', this.doResize);
        window.removeEventListener('mouseup', this.stopResize);
        this.resizeData = null;
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
            // Próba odczytu JSON (tak jak miałeś)
            const data = JSON.parse(e.dataTransfer?.getData('application/json') || '{}');
            shiftId = data.id;
        } catch (err) {
            // Backup (tak jak miałeś)
            shiftId = e.dataTransfer?.getData('text') || this.activeDragId || '';
        }

        const container = (e.currentTarget as HTMLElement).querySelector('.day-content') as HTMLElement;
        const rect = container.getBoundingClientRect();
        const x = e.clientX - rect.left;

        const rawMinute = (x / rect.width) * 1440;
        let snappedMinute = this.snapTo15Minutes(rawMinute);
        if (snappedMinute >= 1440) {
            snappedMinute = 1425;
        }
        const minute = Math.max(0, Math.min(1439, snappedMinute));

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

    private snapTo15Minutes(minutes: number): number {
        // 15 minut to nasz "krok"
        return Math.round(minutes / 15) * 15;
    }

    private updateTooltip(e: MouseEvent | DragEvent, text: string) {
        const tooltip = document.getElementById('schedule-tooltip');
        if (tooltip) {
            tooltip.innerText = text;

            const offset = 25;

            tooltip.style.left = `${e.clientX}px`;
            tooltip.style.top = `${e.clientY - offset}px`;
            tooltip.classList.add('visible');
        }
    }

    private hideTooltip() {
        const tooltip = document.getElementById('schedule-tooltip');
        tooltip?.classList.remove('visible');
    }

    private formatMinutesToTime(totalMinutes: number): string {
        const h = Math.floor(totalMinutes / 60);
        const m = totalMinutes % 60;
        return m > 0 ? `${h}h ${m}m` : `${h}h`;
    }
}

if (!customElements.get('native-schedule-grid')) {
    customElements.define('native-schedule-grid', NativeScheduleGrid);
}