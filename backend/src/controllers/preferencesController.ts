import { Request, Response } from "express";
import pg from "../utils/db"; // seu pool de conexão

export const updatePreferences = async (req: Request, res: Response) => {
  const userId = req.userId; // obtido do auth middleware
  const { theme, language } = req.body;

  if (!["light", "dark"].includes(theme) || !["pt", "en"].includes(language)) {
    return res.status(400).json({ error: "Preferências inválidas" });
  }

  await pg.query(
    "UPDATE users SET theme=$1, language=$2 WHERE id=$3",
    [theme, language, userId]
  );

  res.json({ message: "Preferências atualizadas com sucesso" });
};
