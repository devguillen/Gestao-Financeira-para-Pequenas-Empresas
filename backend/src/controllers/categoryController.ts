import { Request, Response } from "express";
import { pool } from "../utils/db";
import { Category } from "../models/categoryModel";

// Criar categoria
export const createCategory = async (req: Request, res: Response) => {
  try {
    // sempre extrair as variáveis do body (evita ReferenceError)
    const { name, type, company_id } = req.body as Partial<Category>;

    if (!name || !type) {
      return res.status(400).json({ message: "name e type são obrigatórios" });
    }

    const result = await pool.query(
      "INSERT INTO categories (name, type, company_id) VALUES ($1, $2, $3) RETURNING *",
      [name, type, company_id ?? null]
    );

    return res.status(201).json(result.rows[0]);
  } catch (err: any) {
    console.error("createCategory error:", err);
    return res.status(500).json({ error: err.message ?? err });
  }
};

// Listar categorias (opcional filtro por company_id e/ou type)
export const getCategories = async (req: Request, res: Response) => {
  try {
    const { company_id, type } = req.query;

    let sql = "SELECT * FROM categories";
    const conditions: string[] = [];
    const params: any[] = [];
    let idx = 1;

    if (company_id) {
      conditions.push(`company_id = $${idx++}`);
      params.push(Number(company_id));
    }
    if (type) {
      conditions.push(`type = $${idx++}`);
      params.push(String(type));
    }

    if (conditions.length) sql += " WHERE " + conditions.join(" AND ");
    sql += " ORDER BY created_at DESC";

    const result = await pool.query(sql, params);
    res.json(result.rows);
  } catch (err: any) {
    console.error("getCategories error:", err);
    res.status(500).json({ error: err.message ?? err });
  }
};

export const getCategoryById = async (req: Request, res: Response) => {
  try {
    const { id } = req.params;
    const result = await pool.query("SELECT * FROM categories WHERE id = $1", [id]);
    if (result.rowCount === 0) return res.status(404).json({ message: "Categoria não encontrada" });
    res.json(result.rows[0]);
  } catch (err: any) {
    console.error("getCategoryById error:", err);
    res.status(500).json({ error: err.message ?? err });
  }
};

export const updateCategory = async (req: Request, res: Response) => {
  try {
    const { id } = req.params;
    const { name, type } = req.body as Partial<Category>;

    const existing = await pool.query("SELECT * FROM categories WHERE id = $1", [id]);
    if (existing.rowCount === 0) return res.status(404).json({ message: "Categoria não encontrada" });

    const updated = await pool.query(
      "UPDATE categories SET name = $1, type = $2 WHERE id = $3 RETURNING *",
      [name ?? existing.rows[0].name, type ?? existing.rows[0].type, id]
    );

    res.json(updated.rows[0]);
  } catch (err: any) {
    console.error("updateCategory error:", err);
    res.status(500).json({ error: err.message ?? err });
  }
};

export const deleteCategory = async (req: Request, res: Response) => {
  try {
    const { id } = req.params;
    const result = await pool.query("DELETE FROM categories WHERE id = $1", [id]);
    if (result.rowCount === 0) return res.status(404).json({ message: "Categoria não encontrada" });
    res.status(204).send();
  } catch (err: any) {
    console.error("deleteCategory error:", err);
    res.status(500).json({ error: err.message ?? err });
  }
};
