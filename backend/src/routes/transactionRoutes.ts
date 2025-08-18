import { Router } from "express";
import {
  getTransactions,
  createTransaction,
  updateTransaction,
  deleteTransaction
} from "../controllers/transactionController";

import { authMiddleware } from "../middleware/authMiddleware";

const router = Router();

// Usando o middleware (mesmo sem JWT, ele define userId fixo)
router.use(authMiddleware);

// Listar transações
router.get("/", getTransactions);

// Criar transação
router.post("/", createTransaction);

// Atualizar transação
router.put("/:id", updateTransaction);

// Deletar transação
router.delete("/:id", deleteTransaction);

export default router;
