// routes/reportRoutes.ts
import { Router } from 'express';
import { getCashflowReport } from '../controllers/reportController';
import { authMiddleware } from '../middleware/authMiddleware';

const router = Router();
router.use(authMiddleware);

router.get('/cashflow', getCashflowReport);

export default router;
