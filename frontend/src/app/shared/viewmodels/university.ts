import { Location } from './location';

export interface University {
  id: number;
  name: string;
  location: Location;
  logo: string | null;
}
