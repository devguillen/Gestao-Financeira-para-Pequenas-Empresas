import { Router, Request, Response } from "express";
import { getPreferences, updatePreferences } from "../controllers/preferencesController";
import { authMiddleware } from "../middlewares/authMiddleware";

const router = Router();

// Recuperar preferências do usuário
router.get("/", authMiddleware, async (req: Request, res: Response) => {
  try {
    await getPreferences(req, res);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Erro ao buscar preferências" });
  }
});

// Atualizar preferências do usuário
router.put("/", authMiddleware, async (req: Request, res: Response) => {
  try {
    await updatePreferences(req, res);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Erro ao atualizar preferências" });
  }
});

export default router;
