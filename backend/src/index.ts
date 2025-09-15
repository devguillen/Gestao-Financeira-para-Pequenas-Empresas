import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import cron from 'node-cron';
import authRoutes from "./routes/authRoutes";
import userRoutes from "./routes/userRoutes";
import transactionRoutes from "./routes/transactionRoutes";
import recurringTransactionsRoutes from './routes/recurringTransactions';
import { processRecurringTransactions } from '../src/controllers/RecurringTransactionController';

dotenv.config();

const app = express();
app.use(cors({
    origin: "http://localhost:3000", 
    credentials: true
  }));
app.use(express.json());

// Rotas
app.use("/api/auth", authRoutes);
app.use("/api/users", userRoutes);
app.use("/api/transactions", transactionRoutes);
app.use('/recurring-transactions', recurringTransactionsRoutes);

// Cron diário às 00:00 para processar transações recorrentes
cron.schedule('0 0 * * *', async () => {
  console.log('Processando transações recorrentes...');
  await processRecurringTransactions();
});


const PORT = process.env.PORT || 3333;
app.listen(PORT, () => console.log(`Servidor rodando na porta ${PORT}`));

const categoryRoutes = require('./routes/categoryRoutes');
app.use('/api/categories', categoryRoutes);

import installmentRoutes from "./routes/installmentRoutes";
app.use("/api/installments", installmentRoutes);




