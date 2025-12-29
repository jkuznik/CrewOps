import { Calendar } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin, { Draggable } from '@fullcalendar/interaction';

export class ScheduleFullCalendar extends HTMLElement {
    private calendar: Calendar | null = null;

    connectedCallback() {
        if (this.calendar) return; // Unikamy podwójnej inicjalizacji

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
            droppable: true,
            initialView: 'dayGridMonth',
            editable: true,
            firstDay: 1, // Ustawia poniedziałek jako pierwszy dzień tygodnia
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth,timeGridWeek'
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
}

customElements.define('schedule-full-calendar', ScheduleFullCalendar);