import { Itinerary } from "./PlanRequest.interface";

export interface ItineraryResponse {
  destination: string;
  days: number;
  pace: string;
  itinerary: Itinerary[];
}
