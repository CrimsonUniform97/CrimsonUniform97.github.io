package com.mojang.brainfuck;

import java.io.IOException;


public class BrainFuckInterpreter
{
    private static final class Cluster
    {
        public byte[] cells = new byte[256];

        public Cluster next;
        public Cluster last;

        public Cluster getNext()
        {
            if (next == null)
            {
                next = new Cluster();
                next.last = this;
            }
            return next;
        }

        public Cluster getLast()
        {
            if (last == null)
            {
                last = new Cluster();
                last.next = this;
            }
            return last;
        }
    }
    
    private static final char PTR_INC = '>';
    private static final char PTR_DEC = '<';
    private static final char VAL_INC = '+';
    private static final char VAL_DEC = '-';
    private static final char OUTPUT = '.';
    private static final char INPUT = ',';
    private static final char LOOP_START = '[';
    private static final char LOOP_END = ']';

    public BrainFuckInterpreter()
    {
    }

    public void run(char[] program) throws IOException
    {
        Cluster cluster = new Cluster();
        int p = 0;
        byte[] b = cluster.cells;
        for (int i = 0; i < program.length; i++)
        {
            char cmd = program[i];
            switch (cmd)
            {
                case PTR_INC:
                    if (++p == 256)
                    {
                        p = 0;
                        cluster = cluster.getNext();
                        b = cluster.cells;
                    }
                    break;
                case PTR_DEC:
                    if (--p == -1)
                    {
                        p = 255;
                        cluster = cluster.getLast();
                        b = cluster.cells;
                    }
                    break;
                case VAL_INC:
                    ++b[p];
                    break;
                case VAL_DEC:
                    --b[p];
                    break;
                case OUTPUT:
                    System.out.print((char) b[p]);
                    break;
                case INPUT:
                    b[p] = (byte) System.in.read();
                    break;
                case LOOP_START:
                    if (cluster.cells[p] == 0)
                    {
                        int depth = 1;
                        do
                        {
                            char tmp = program[++i];
                            if (tmp == '[') depth++;
                            if (tmp == ']') depth--;
                        }
                        while (depth > 0);
                    }
                    break;
                case LOOP_END:
                    if (cluster.cells[p] != 0)
                    {
                        int depth = 1;
                        do
                        {
                            char tmp = program[--i];
                            if (tmp == '[') depth--;
                            if (tmp == ']') depth++;
                        }
                        while (depth > 0);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static void main(String[] args) throws IOException
    {
        // Test a simple hello world application
        new BrainFuckInterpreter().run("++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.".toCharArray());
    }
}