// controllers/RecurringTransactionController.ts
import { Request, Response } from 'express';
import * as Recurring from '../models/RecurringTransaction';
import * as TransactionModel from '../models/transactionModel';

// Criar uma transação recorrente
export const createRecurring = async (req: Request, res: Response) => {
  const { user_id, type, category, amount, description, day_of_month } = req.body;

  if (!user_id || !type || !category || !amount || !day_of_month) {
    return res.status(400).json({ error: 'Campos obrigatórios faltando' });
  }

  try {
    const rec = await Recurring.createRecurringTransaction({
      user_id,
      type,
      category,
      amount,
      description,
      day_of_month,
    });
    res.status(201).json(rec);
  } catch (err) {
    res.status(500).json({ error: 'Erro ao criar recorrência', details: err });
  }
};

// Listar transações recorrentes de um usuário
export const getRecurringByUser = async (req: Request, res: Response) => {
  const user_id = req.userId || 1; // usar userId do middleware ou fixo para teste

  try {
    const recs = await Recurring.getRecurringTransactionsByUser(user_id);
    res.json(recs);
  } catch (err) {
    res.status(500).json({ error: 'Erro ao buscar recorrências', details: err });
  }
};

// Processar transações recorrentes (para cron diário)
export const processRecurringTransactions = async () => {
  const day_of_month = new Date().getDate();
  const pending = await Recurring.getPendingRecurringTransactions(day_of_month);

  for (const rec of pending) {
    try {
      await TransactionModel.createTransaction({
        user_id: rec.user_id,
        type: rec.type,
        category: rec.category,
        amount: rec.amount,
        description: rec.description,
        date: new Date(),
      });
      await Recurring.updateLastExecuted(rec.id);
    } catch (err) {
      console.error(`Erro ao processar transação recorrente ID ${rec.id}:`, err);
    }
  }
};
