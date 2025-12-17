export interface ShiftDTO {
    id: string;
    name: string;
    color: string;
}

export interface ShiftResource {
    shiftDTO: ShiftDTO;
    startMinute: number;      // zastępuje startSlot (0-1439)
    durationMinutes: number;  // zastępuje durationInSlots
    isCrossMidnight: boolean; // odpowiednik isCrossMidnightSegment
    hasCrossMidnight: boolean;// odpowiednik hasCrossMidnightSegment
}

export interface ScheduleDay {
    dayNumber: number;
    shifts: ShiftResource[];
}