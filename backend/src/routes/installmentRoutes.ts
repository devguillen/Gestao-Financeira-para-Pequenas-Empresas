import express from "express";
import {
  createInstallments,
  getInstallments,
  updateInstallment,
  deleteInstallment,
} from "../controllers/installmentController";
import { authMiddleware } from "../middleware/authMiddleware";

const router = express.Router();
router.use(authMiddleware);

router.post("/", createInstallments); // cria várias parcelas
router.get("/:transactionId", getInstallments);
router.put("/:id", updateInstallment);
router.delete("/:id", deleteInstallment);

export default router;
