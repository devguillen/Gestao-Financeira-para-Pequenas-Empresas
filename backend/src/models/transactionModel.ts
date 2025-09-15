
import { pool } from '../utils/db';

// Criar uma nova transação
export const createTransaction = async (transaction: {
  user_id: number;
  type: "income" | "expense";
  category: string;
  amount: number;
  description?: string;
  date: Date;
}) => {
  const { user_id, type, category, amount, description, date } = transaction;

  if (!user_id || !type || !category || !amount || !date) {
    throw new Error('Campos obrigatórios faltando para criar transação');
  }

  const result = await pool.query(
    `INSERT INTO transactions (user_id, type, category, amount, description, date)
     VALUES ($1, $2, $3, $4, $5, $6)
     RETURNING *`,
    [user_id, type, category, amount, description || null, date]
  );

  return result.rows[0];
};

// Listar transações do usuário
export const getTransactionsByUser = async (user_id: number) => {
  const result = await pool.query(
    `SELECT * FROM transactions WHERE user_id = $1 ORDER BY date DESC`,
    [user_id]
  );
  return result.rows;
};

// Atualizar transação
export const updateTransaction = async (transaction: {
  id: number;
  user_id: number;
  type: "income" | "expense";
  category: string;
  amount: number;
  description?: string;
  date: Date;
}) => {
  const { id, user_id, type, category, amount, description, date } = transaction;

  const result = await pool.query(
    `UPDATE transactions 
     SET type=$1, category=$2, amount=$3, description=$4, date=$5
     WHERE id=$6 AND user_id=$7
     RETURNING *`,
    [type, category, amount, description || null, date, id, user_id]
  );

  return result.rows[0];
};

// Deletar transação
export const deleteTransaction = async (id: number, user_id: number) => {
  await pool.query(
    `DELETE FROM transactions WHERE id=$1 AND user_id=$2`,
    [id, user_id]
  );
  return { message: 'Transaction deleted successfully' };
};
