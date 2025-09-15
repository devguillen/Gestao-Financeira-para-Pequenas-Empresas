// models/RecurringTransaction.ts
import { pool } from '../utils/db';

export const createRecurringTransaction = async ({
  user_id,
  type,
  category,
  amount,
  description,
  day_of_month,
}: {
  user_id: number;
  type: "income" | "expense";
  category: string;
  amount: number;
  description?: string;
  day_of_month: number;
}) => {
  const result = await pool.query(
    `INSERT INTO recurring_transactions 
     (user_id, type, category, amount, description, day_of_month) 
     VALUES ($1,$2,$3,$4,$5,$6) RETURNING *`,
    [user_id, type, category, amount, description || null, day_of_month]
  );
  return result.rows[0];
};

export const getRecurringTransactionsByUser = async (user_id: number) => {
  const result = await pool.query(
    `SELECT * FROM recurring_transactions WHERE user_id = $1`,
    [user_id]
  );
  return result.rows;
};

export const getPendingRecurringTransactions = async (day_of_month: number) => {
  const result = await pool.query(
    `SELECT * FROM recurring_transactions 
     WHERE day_of_month = $1 AND 
     (last_executed IS NULL OR last_executed < date_trunc('month', CURRENT_DATE))`,
    [day_of_month]
  );
  return result.rows;
};

export const updateLastExecuted = async (id: number) => {
  await pool.query(
    `UPDATE recurring_transactions SET last_executed = NOW(), updated_at = NOW() WHERE id = $1`,
    [id]
  );
};
