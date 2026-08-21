export interface PlanRequest {
    name: string;
    usuario_id: number;
    destination: string;
    days: number;
    pace: string;
    url?: string;
    lugares: Lugar[];
    itinerary: Itinerary[];
}

export interface Lugar{
    name: string;
    direction: string;
    category: string;
}

export interface Itinerary{
    day: number;
    title: string;
    summary: string;
    stops: Stop[];
}

export interface Stop{
    time: string;
    name: string;
    note: string;
}

export interface PlanByIdResponse extends PlanRequest {
    id: number;
}