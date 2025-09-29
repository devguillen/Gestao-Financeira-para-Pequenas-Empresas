import { Entity, PrimaryGeneratedColumn, Column, BaseEntity } from "typeorm";
import { IsEmail, Length } from "class-validator";

@Entity()
export class User extends BaseEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  @Length(3, 50)
  name: string;

  @Column({ unique: true })
  @IsEmail()
  email: string;

  @Column()
  @Length(6, 100)
  password: string;

  @Column({ default: "user" })
  role: string;
}
