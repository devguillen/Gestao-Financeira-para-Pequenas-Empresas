import { Request, Response } from "express";
import { pool } from "../utils/db";

// Listar transações do usuário
export const getTransactions = async (req: Request, res: Response) => {
  try {
    const userId = req.userId || 1; // userId fixo para teste
    const result = await pool.query(
      "SELECT * FROM transactions WHERE user_id=$1 ORDER BY date DESC",
      [userId]
    );
    res.json(result.rows);
  } catch (err) {
    res.status(500).json({ error: err });
  }
};

// Criar nova transação
export const createTransaction = async (req: Request, res: Response) => {
  try {
    const userId = req.userId || 1; // userId fixo
    const { type, category, amount, description, date } = req.body;

    const result = await pool.query(
      "INSERT INTO transactions (user_id, type, category, amount, description, date) VALUES ($1,$2,$3,$4,$5,$6) RETURNING *",
      [userId, type, category, amount, description, date]
    );
    res.json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: err });
  }
};

// Atualizar transação
export const updateTransaction = async (req: Request, res: Response) => {
  try {
    const userId = req.userId || 1; // userId fixo
    const { id } = req.params;
    const { type, category, amount, description, date } = req.body;

    const result = await pool.query(
      "UPDATE transactions SET type=$1, category=$2, amount=$3, description=$4, date=$5 WHERE id=$6 AND user_id=$7 RETURNING *",
      [type, category, amount, description, date, id, userId]
    );
    res.json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: err });
  }
};

// Deletar transação
export const deleteTransaction = async (req: Request, res: Response) => {
  try {
    const userId = req.userId || 1; // userId fixo
    const { id } = req.params;

    await pool.query("DELETE FROM transactions WHERE id=$1 AND user_id=$2", [id, userId]);
    res.json({ message: "Transaction deleted successfully" });
  } catch (err) {
    res.status(500).json({ error: err });
  }
};
