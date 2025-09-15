import { Router } from 'express';
import { createRecurring, getRecurringByUser } from '../controllers/RecurringTransactionController';
import { authMiddleware } from '../middleware/authMiddleware';

const router = Router();

// Aplica autenticação em todas as rotas deste router
router.use(authMiddleware);

// Criar nova transação recorrente
router.post('/', createRecurring);

// Listar transações recorrentes do usuário
router.get('/', getRecurringByUser);

export default router;
