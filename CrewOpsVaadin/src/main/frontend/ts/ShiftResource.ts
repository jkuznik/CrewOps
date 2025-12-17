export interface ShiftResource {
    id: string; // UUID
    shiftId: string; // ID bazowego ShiftDTO
    name: string;
    color: string;
    startSlot: number; // index
    durationInSlots: number;
    isCrossMidnight: boolean;
    // Dane pomocnicze do obliczeń podczas przesuwania
    beforeMoveStartSlot?: number;
}