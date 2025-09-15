export interface Category {
  id?: number;
  name: string;
  type: "income" | "expense";
  company_id?: number | null;
  created_at?: Date;
}
