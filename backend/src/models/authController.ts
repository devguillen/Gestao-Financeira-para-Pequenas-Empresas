import { Request, Response } from "express";
import { pool } from "../utils/db";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
import dotenv from "dotenv";
dotenv.config();

const saltRounds = 10;

export const register = async (req: Request, res: Response) => {
  try {
    const { name, email, password } = req.body;
    const hashedPassword = await bcrypt.hash(password, saltRounds);
    const result = await pool.query(
      "INSERT INTO users (name, email, password) VALUES ($1,$2,$3) RETURNING id, name, email",
      [name, email, hashedPassword]
    );
    res.json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: err });
  }
};

export const login = async (req: Request, res: Response) => {
  try {
    const { email, password } = req.body;
    const user = await pool.query("SELECT * FROM users WHERE email=$1", [email]);
    if (user.rows.length === 0) return res.status(400).json({ message: "User not found" });

    const valid = await bcrypt.compare(password, user.rows[0].password);
    if (!valid) return res.status(400).json({ message: "Invalid password" });

    const token = jwt.sign({ id: user.rows[0].id }, process.env.JWT_SECRET!, { expiresIn: "1d" });
    res.json({ token, user: { id: user.rows[0].id, name: user.rows[0].name, email } });
  } catch (err) {
    res.status(500).json({ error: err });
  }
};
