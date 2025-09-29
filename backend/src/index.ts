import "reflect-metadata";
import express from "express";
import { DataSource } from "typeorm";
import authRoutes from "./routes/authRoutes";
import { User } from "./entities/User";

const app = express();
app.use(express.json());

app.use("/auth", authRoutes);

const AppDataSource = new DataSource({
  type: "postgres",
  host: "localhost",
  port: 5432,
  username: "postgres",
  password: "sua_senha",
  database: "finance_app",
  synchronize: true,
  logging: false,
  entities: [User],
});

AppDataSource.initialize()
  .then(() => {
    app.listen(3000, () => console.log("Server running on http://localhost:3000"));
  })
  .catch((error) => console.log(error));
