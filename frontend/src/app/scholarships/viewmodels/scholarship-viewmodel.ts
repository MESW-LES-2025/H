export interface ScholarshipDTO {
  id: number;
  name: string;
  description: string;
  amount: number;
  courseType: string;
  universityId: number;
  universityName: string;
}

export interface ScholarshipVM {
  id: string;
  name: string;
  description: string;
  amount: number;
  courseType: string;
  universityId: string;
  universityName: string;
  color: string;
}

export function toScholarshipVM(dto: ScholarshipDTO): ScholarshipVM {
  // Generate a color based on course type
  let color = '#7DB19F'; // default
  if (dto.courseType === 'BACHELOR') {
    color = '#5DADE2';
  } else if (dto.courseType === 'MASTER') {
    color = '#F39C12';
  } else if (dto.courseType === 'DOCTORATE') {
    color = '#E74C3C';
  }

  return {
    id: dto.id.toString(),
    name: dto.name,
    description: dto.description || 'No description available',
    amount: dto.amount,
    courseType: dto.courseType,
    universityId: dto.universityId.toString(),
    universityName: dto.universityName,
    color: color,
  };
}

export interface ScholarshipFilters {
  search?: string;
  courseType?: string;
  minAmount?: number;
  maxAmount?: number;
}
