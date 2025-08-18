// src/routes/exportRoutes.ts
import { Router } from "express";
import { exportTransactionsCSV, exportTransactionsPDF } from "../controllers/exportController";
import { authMiddleware } from "../middleware/authMiddleware";

const router = Router();

// Middleware para pegar userId
router.use(authMiddleware);

// Endpoints de exportação
router.get("/csv", exportTransactionsCSV);
router.get("/pdf", exportTransactionsPDF);

export default router;
