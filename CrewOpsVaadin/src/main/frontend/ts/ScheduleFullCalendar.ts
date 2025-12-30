import { Calendar } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import allLocales from '@fullcalendar/core/locales-all';
import interactionPlugin, { Draggable } from '@fullcalendar/interaction';

export class ScheduleFullCalendar extends HTMLElement {
    private calendar: Calendar | null = null;
    private _locale: string = 'en';

    set locale(value: string) {
        this._locale = value;
        if (this.calendar) {
            this.calendar.setOption('locale', value);
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

    private initCalendar(el: HTMLElement) {
        new Draggable(document.body, {
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
            locales: allLocales, // Rejestrujemy bazę języków
            locale: this._locale, // Ustawiamy początkowy język
            droppable: true,
            initialView: 'dayGridMonth',
            editable: true,
            headerToolbar: {
                left: 'prev today',
                center: 'title',
                right: 'next',
            },
            dropAccept: '.calendar-template-item',
            dragRevertDuration: 0,

            eventDragStart: () => console.log("Start przeciągania wewnątrz kalendarza"),
            drop: (info) => {
                const draggedEl = info.draggedEl;
                const templateData = draggedEl.getAttribute('data-template') || '{}';

                this.dispatchEvent(new CustomEvent('template-dropped', {
                    detail: {
                        date: info.dateStr,
                        template: JSON.parse(templateData)
                    },
                    bubbles: true,
                    composed: true
                }));
            },

            dayCellDidMount: (info) => {
                const day = info.date.getDay(); // 0 = Niedziela, 6 = Sobota

                if (day === 0) {
                    // Niedziela - wyraźniejszy czerwony (5% krycia)
                    info.el.style.backgroundColor = 'rgba(255, 0, 0, 0.08)';
                } else if (day === 6) {
                    // Sobota - bardzo delikatny szary lub jasnoczerwony (3% krycia)
                    info.el.style.backgroundColor = 'rgba(255, 0, 0, 0.03)';
                }
            }
        });

        this.calendar.render();
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

customElements.define('schedule-full-calendar', ScheduleFullCalendar);