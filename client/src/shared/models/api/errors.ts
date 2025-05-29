export interface BadRequestBody {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  errors: string[];
}

export interface NotFoundErrorBody {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
}