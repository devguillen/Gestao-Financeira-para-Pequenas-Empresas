import { Router } from "express";
import { register, login } from "../controllers/authController";

const router = Router();

// Cadastro de usuário
router.post("/register", register);

// Login de usuário
router.post("/login", login);

export default router;
