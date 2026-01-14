import { Calendar } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import allLocales from '@fullcalendar/core/locales-all';
import interactionPlugin, { Draggable as ExternalDraggable } from '@fullcalendar/interaction';

export class ScheduleFullCalendar extends HTMLElement {
    private calendar: Calendar | null = null;
    private _draggableInstance: ExternalDraggable | null = null;
    private _locale: string = 'en';
    private _hidden: boolean = false;

    set locale(value: string) {
        this._locale = value;
        if (this.calendar) {
            this.calendar.setOption('locale', value);
        }
    }

    set hiddenMode(value: boolean) {
        this._hidden = value;
        this.style.display = value ? 'none' : 'flex';

        if (!value && this.calendar) {
            setTimeout(() => this.calendar?.updateSize(), 50);
        }
    }

    connectedCallback() {
        if (this.calendar) return;

        this.innerHTML = '';
        const calendarEl = document.createElement('div');
        calendarEl.style.height = '100%';
        this.appendChild(calendarEl);

        this.initCalendar(calendarEl);
    }

    disconnectedCallback() {
        // Sprzątanie instancji Draggable
        if (this._draggableInstance) {
            this._draggableInstance.destroy();
            this._draggableInstance = null;
        }
        // Sprzątanie instancji Kalendarza
        if (this.calendar) {
            this.calendar.destroy();
            this.calendar = null;
        }
    }

    private initCalendar(el: HTMLElement) {
        // Inicjalizacja Draggable z przypisaniem do nowej nazwy zmiennej
        this._draggableInstance = new ExternalDraggable(document.body, {
            itemSelector: '.calendar-template-item[draggable="true"]',
            eventData: (eventEl) => {
                const dataRaw = eventEl.getAttribute('data-template');
                const data = dataRaw ? JSON.parse(dataRaw) : {};
                return {
                    title: data.title || eventEl.innerText,
                    duration: { days: data.duration || 1 },
                    backgroundColor: data.color,
                    create: false
                };
            }
        });

        this.calendar = new Calendar(el, {
            plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
            locales: allLocales,
            locale: this._locale,
            droppable: true,
            initialView: 'dayGridMonth',
            editable: true,
            headerToolbar: {
                left: 'title',
                center: '',
                right: 'prev today, next',
            },
            dropAccept: '.calendar-template-item',
            dragRevertDuration: 0,

            drop: (info) => {
                if (info.jsEvent) {
                    info.jsEvent.stopPropagation();
                }

                const draggedEl = info.draggedEl;
                const templateData = draggedEl.getAttribute('data-template') || '{}';

                this.dispatchEvent(new CustomEvent('template-dropped', {
                    detail: {
                        date: info.dateStr,
                        template: JSON.parse(templateData)
                    },
                    bubbles: false,
                    composed: true
                }));
            },

            dayCellDidMount: (info) => {
                const day = info.date.getDay();

                if (day === 0) {
                    info.el.style.backgroundColor = 'rgba(255, 0, 0, 0.08)';
                } else if (day === 6) {
                    info.el.style.backgroundColor = 'rgba(255, 0, 0, 0.03)';
                }
            },

            dateClick: (info) => {
                this.dispatchEvent(new CustomEvent('date-selected', {
                    detail: { date: info.dateStr },
                    bubbles: false,
                    composed: true
                }));
            }
        });

        this.calendar.render();
        this.style.display = this._hidden ? 'none' : 'flex';
    }

    addEvent(id: string, title: string, start: string, end: string, color: string) {
        if (this.calendar) {
            this.calendar.addEvent({
                id: id,
                title: title,
                start: start,
                end: end,
                backgroundColor: color,
                borderColor: color
            });
        }
    }

    updateSize() {
        if (this.calendar) {
            this.calendar.updateSize();
        }
    }
}

if (!customElements.get('schedule-full-calendar')) {
    customElements.define('schedule-full-calendar', ScheduleFullCalendar);
}