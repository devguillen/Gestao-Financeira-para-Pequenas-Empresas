import { Request, Response } from "express";
import { User } from "../entities/User";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";

export const registerUser = async (req: Request, res: Response) => {
  const { name, email, password } = req.body;

  try {
    const existingUser = await User.findOne({ where: { email } });
    if (existingUser) return res.status(400).json({ message: "Email já cadastrado" });

    const hashedPassword = await bcrypt.hash(password, 10);

    const user = User.create({ name, email, password: hashedPassword });
    await user.save();

    const token = jwt.sign({ id: user.id, email: user.email }, process.env.JWT_SECRET!, { expiresIn: "1h" });

    res.status(201).json({ user: { id: user.id, name: user.name, email: user.email }, token });
  } catch (error) {
    res.status(500).json({ message: "Erro ao registrar usuário", error });
  }
};
