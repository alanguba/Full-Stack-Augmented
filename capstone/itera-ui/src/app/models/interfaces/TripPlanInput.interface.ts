import { PlaceItem } from "./PlaceItem.interface";
import { TripDestination } from "./TripDestination.interface";

export type Pace = 'Relajado' | 'Balanceado' | 'Intenso';

export interface TripPlanInput {
  destination: TripDestination;
  days: number;
  pace: Pace;
  places: PlaceItem[];
}
