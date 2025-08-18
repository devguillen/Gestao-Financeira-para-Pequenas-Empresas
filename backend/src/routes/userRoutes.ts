import { Router } from "express";
import {
  getUsers,
  createUser,
  updateUser,
  deleteUser
} from "../controllers/userController";

const router = Router();

// Listar usuários
router.get("/", getUsers);

// Criar usuário
router.post("/", createUser);

// Atualizar usuário
router.put("/:id", updateUser);

// Deletar usuário
router.delete("/:id", deleteUser);

export default router;
