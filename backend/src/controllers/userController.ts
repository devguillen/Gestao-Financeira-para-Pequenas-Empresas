import { Request, Response } from "express";
import { pool } from "../utils/db";
import bcrypt from "bcrypt";

// Listar todos os usuários
export const getUsers = async (req: Request, res: Response) => {
  try {
    const result = await pool.query("SELECT id, name, email FROM users");
    res.json(result.rows);
  } catch (err) {
    res.status(500).json({ error: err });
  }
};

// Criar novo usuário
export const createUser = async (req: Request, res: Response) => {
  try {
    const { name, email, password } = req.body;
    const hashedPassword = await bcrypt.hash(password, 10);
    const result = await pool.query(
      "INSERT INTO users (name, email, password) VALUES ($1, $2, $3) RETURNING id, name, email",
      [name, email, hashedPassword]
    );
    res.json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: err });
  }
};

// Atualizar usuário
export const updateUser = async (req: Request, res: Response) => {
  try {
    const { id } = req.params;
    const { name, email } = req.body;
    const result = await pool.query(
      "UPDATE users SET name=$1, email=$2 WHERE id=$3 RETURNING id, name, email",
      [name, email, id]
    );
    res.json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: err });
  }
};

// Deletar usuário
export const deleteUser = async (req: Request, res: Response) => {
  try {
    const { id } = req.params;
    await pool.query("DELETE FROM users WHERE id=$1", [id]);
    res.json({ message: "User deleted successfully" });
  } catch (err) {
    res.status(500).json({ error: err });
  }
};
