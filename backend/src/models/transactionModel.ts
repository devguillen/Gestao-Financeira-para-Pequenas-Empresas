export interface Transaction {
    id?: number;
    user_id: number;
    type: "income" | "expense";
    category: string;
    amount: number;
    description?: string;
    date: Date;
  }
  