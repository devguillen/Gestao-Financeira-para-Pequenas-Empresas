export interface Installment {
    id?: number;
    transaction_id: number;
    installment_number: number;
    amount: number;
    due_date: string;   // ISO date
    paid?: boolean;
    created_at?: Date;
  }
  