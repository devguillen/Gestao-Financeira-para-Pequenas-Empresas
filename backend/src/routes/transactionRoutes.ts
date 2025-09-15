import { Router } from "express";
import {
  getTransactions,
  createTransaction,
  updateTransaction,
  deleteTransaction,
  createTransactionWithFile, // import da função de upload
} from "../controllers/transactionController";

import { authMiddleware } from "../middleware/authMiddleware";
import { upload } from "../utils/upload"; // middleware de upload

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

// ------------------------
// Novas rotas de upload
// Criar transação com arquivo PDF ou CSV
router.post(
  "/file",
  upload.single("file"), // o campo do form-data deve ser "file"
  createTransactionWithFile
);

export default router;
