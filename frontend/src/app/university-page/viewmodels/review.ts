export interface Review {
    id?: number;
    userId: number;
    userName?: string;
    universityId: number;
    rating: number;
    title: string;
    description: string;
    reviewDate?: string;
}