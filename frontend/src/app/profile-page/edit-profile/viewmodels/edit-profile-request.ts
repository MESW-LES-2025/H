export interface EditProfileRequest {
  id: number | null;
  name: string | null;
  age: number | null;
  gender: string | null;
  location: string | null;
  jobTitle: string | null;
}