import { Request, Response } from "express";
import { pool } from "../utils/db";
import { Installment } from "../models/installmentModel";

// Criar parcelas (pode receber array para várias de uma vez)
export const createInstallments = async (req: Request, res: Response) => {
  try {
    const { transaction_id, installments } = req.body as {
      transaction_id: number;
      installments: Omit<Installment, "id" | "transaction_id">[];
    };

    const results = [];
    for (const [idx, i] of installments.entries()) {
      const result = await pool.query(
        `INSERT INTO transaction_installments
         (transaction_id, installment_number, amount, due_date, paid)
         VALUES ($1, $2, $3, $4, $5) RETURNING *`,
        [transaction_id, i.installment_number ?? idx + 1, i.amount, i.due_date, i.paid ?? false]
      );
      results.push(result.rows[0]);
    }
    res.status(201).json(results);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
};

// Listar parcelas de uma transação
export const getInstallments = async (req: Request, res: Response) => {
  try {
    const { transactionId } = req.params;
    const result = await pool.query(
      "SELECT * FROM transaction_installments WHERE transaction_id = $1 ORDER BY installment_number",
      [transactionId]
    );
    res.json(result.rows);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
};

// Atualizar parcela (ex.: marcar como paga)
export const updateInstallment = async (req: Request, res: Response) => {
  try {
    const { id } = req.params;
    const { amount, due_date, paid } = req.body;
    const result = await pool.query(
      `UPDATE transaction_installments
       SET amount = COALESCE($1, amount),
           due_date = COALESCE($2, due_date),
           paid = COALESCE($3, paid)
       WHERE id = $4
       RETURNING *`,
      [amount, due_date, paid, id]
    );
    if (result.rowCount === 0)
      return res.status(404).json({ message: "Parcela não encontrada" });
    res.json(result.rows[0]);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
};

// Excluir parcela
export const deleteInstallment = async (req: Request, res: Response) => {
  try {
    const { id } = req.params;
    const result = await pool.query(
      "DELETE FROM transaction_installments WHERE id = $1",
      [id]
    );
    if (result.rowCount === 0)
      return res.status(404).json({ message: "Parcela não encontrada" });
    res.status(204).send();
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
};
