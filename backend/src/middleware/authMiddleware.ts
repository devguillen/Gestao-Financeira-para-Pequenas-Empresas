import { Request, Response, NextFunction } from "express";

// Middleware sem JWT, só passa adiante
export const authMiddleware = (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  // Se quiser, você pode colocar um userId fixo para teste
  req.userId = 1; // opcional, para usar em CRUD de transações
  next();
};

// extendendo o Request do express para ter userId
declare module "express-serve-static-core" {
  interface Request {
    userId?: number;
  }
}
