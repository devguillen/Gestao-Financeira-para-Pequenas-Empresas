import { plainToClass } from "class-transformer";
import { validate, ValidationError } from "class-validator";
import { Request, Response, NextFunction } from "express";

export function validationMiddleware(type: any) {
  return (req: Request, res: Response, next: NextFunction) => {
    const errors: ValidationError[] = [];
    const dto = plainToClass(type, req.body);

    validate(dto).then((validationErrors) => {
      if (validationErrors.length > 0) {
        return res.status(400).json(validationErrors);
      } else {
        next();
      }
    });
  };
}
