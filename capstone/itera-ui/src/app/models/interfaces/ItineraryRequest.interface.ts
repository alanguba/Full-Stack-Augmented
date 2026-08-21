import { Pace } from './TripPlanInput.interface';

export interface ItineraryRequest {
  destination: string; // or { label: string } if you decided that in backend
  days: number;
  pace: Pace;
  places: Array<{
    id: string;
    name: string;
    address?: string;
    category?: string;
  }>;
}
