// controllers/reportController.ts
import { Request, Response } from 'express';
import { pool } from '../utils/db';

export const getCashflowReport = async (req: Request, res: Response) => {
  const user_id = req.userId || 1;
  const { startDate, endDate } = req.query;

  try {
    const result = await pool.query(
      `SELECT type, category, SUM(amount) as total
       FROM transactions
       WHERE user_id = $1 AND date BETWEEN $2 AND $3
       GROUP BY type, category`,
      [user_id, startDate, endDate]
    );

    res.json(result.rows);
  } catch (err) {
    res.status(500).json({ error: console.error || err });
  }
};