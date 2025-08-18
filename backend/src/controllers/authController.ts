import { Request, Response } from "express";
import { pool } from "../utils/db";
import bcrypt from "bcrypt";

// Registrar usuário (Cadastro)
export const register = async (req: Request, res: Response) => {
  try {
    const { name, email, password } = req.body;

    // Verifica se usuário já existe
    const userExists = await pool.query("SELECT * FROM users WHERE email=$1", [email]);
    if (userExists.rows.length > 0) {
      return res.status(400).json({ message: "User already exists" });
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    const result = await pool.query(
      "INSERT INTO users (name, email, password) VALUES ($1,$2,$3) RETURNING id, name, email",
      [name, email, hashedPassword]
    );

    res.json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: err });
  }
};

// Login do usuário (sem JWT)
export const login = async (req: Request, res: Response) => {
  try {
    const { email, password } = req.body;

    const userResult = await pool.query("SELECT * FROM users WHERE email=$1", [email]);
    if (userResult.rows.length === 0) {
      return res.status(400).json({ message: "User not found" });
    }

    const user = userResult.rows[0];

    const validPassword = await bcrypt.compare(password, user.password);
    if (!validPassword) {
      return res.status(400).json({ message: "Invalid password" });
    }

    // Retorna apenas dados do usuário, sem token
    res.json({
      id: user.id,
      name: user.name,
      email: user.email
    });
  } catch (err) {
    res.status(500).json({ error: err });
  }
};
