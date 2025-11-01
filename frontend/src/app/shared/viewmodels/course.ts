import {University} from './university';

export interface Course {
  id: number;
  name: string;
  area: string;
  university: University;
}
