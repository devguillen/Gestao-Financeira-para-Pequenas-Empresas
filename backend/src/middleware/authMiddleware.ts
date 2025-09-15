import { Request, Response, NextFunction } from "express";

// Middleware sem JWT, apenas passa adiante
export const authMiddleware = (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  req.userId = 1; // opcional, para teste
  next();
};

// Extensão da interface Request para incluir userId
declare module "express-serve-static-core" {
  interface Request {
    userId?: number;
  }
}
