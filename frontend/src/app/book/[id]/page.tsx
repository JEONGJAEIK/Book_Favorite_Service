import { Margarine } from "next/font/google";
import Image from "next/image";
import { CiSearch } from "react-icons/ci";
import "@/lib/searchBook.css";
import client from "@/lib/client";

import ClientPage from "./ClientPage";

export default async function BookList() {
  return <ClientPage></ClientPage>;
}
