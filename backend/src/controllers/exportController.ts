// src/controllers/exportController.ts
import { Request, Response } from "express";
import { pool } from "../utils/db";
import { Parser } from "json2csv";
import PDFDocument from "pdfkit";

// Função para buscar transações do usuário
const getUserTransactions = async (userId: number) => {
  const result = await pool.query(
    "SELECT id, type, category, amount, description, date FROM transactions WHERE user_id = $1 ORDER BY date DESC",
    [userId]
  );
  return result.rows;
};

// Exportar transações em CSV
export const exportTransactionsCSV = async (req: Request, res: Response) => {
  try {
    const userId = req.userId!;
    const transactions = await getUserTransactions(userId);

    const json2csvParser = new Parser();
    const csv = json2csvParser.parse(transactions);

    res.header("Content-Type", "text/csv");
    res.attachment("transactions.csv");
    res.send(csv);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Erro ao exportar CSV" });
  }
};

// Exportar transações em PDF
export const exportTransactionsPDF = async (req: Request, res: Response) => {
  try {
    const userId = req.userId!;
    const transactions = await getUserTransactions(userId);

    const doc = new PDFDocument({ margin: 30, size: "A4" });
    res.setHeader("Content-Type", "application/pdf");
    res.setHeader("Content-Disposition", "attachment; filename=transactions.pdf");

    doc.pipe(res);

    doc.fontSize(20).text("Relatório de Transações", { align: "center" });
    doc.moveDown();

    transactions.forEach((t) => {
      doc
        .fontSize(12)
        .text(
          `ID: ${t.id} | Tipo: ${t.type} | Categoria: ${t.category} | Valor: R$ ${t.amount.toFixed(
            2
          )} | Descrição: ${t.description || "-"} | Data: ${new Date(
            t.date
          ).toLocaleDateString()}`
        );
      doc.moveDown(0.5);
    });

    doc.end();
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Erro ao exportar PDF" });
  }
};
