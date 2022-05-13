package com.example.bruhdroid.model.src

import com.example.bruhdroid.model.src.blocks.Block
/*
for(int i = 0; i < height; i++)
{
    for(int j = 0; j < width; j++)
    {
        int x = j - width / 2;
        int y = -1 * (i - height / 2);
         int firstPart = x * x;
         int secondPart = (int)((y - Math.Sqrt(Math.Abs(x))) * (y - Math.Abs(x)));

          int heartSize = 500; //РАЗМЕР СЕРДЦА

          if (firstPart + secondPart <= heartSize)
          {
              buffer[i, j] = '♥'; //ЗАПОЛНЯЕМ СЕРДЦЕ СИМВОЛОМ КОТОРЫМ ХОТИМ
          }
          else
          {
              buffer[i, j] = ' '; //ЗАПОЛНЯЕМ НЕ НУЖНУЮ НАМ ЧАСТЬ БУФФЕРА СИМВОЛОМ КОТОРЫМ ХОТИМ
          }
     }
}
 */
class TemplateStorage {
    companion object {
        enum class Template {
            BUBBLE_SORT, INFINITY_LOOP
        }

        private val templates = mutableMapOf(
            Template.BUBBLE_SORT to arrayOf(
                Block(Instruction.INIT, "n = 5, *arr[n]"),
                Block(Instruction.INIT, "i = 0, j = 0"),

                Block(Instruction.WHILE, "i < n"),
                    Block(Instruction.INPUT, "arr[i]"),
                    Block(Instruction.SET, "i += 1"),
                Block(Instruction.END_WHILE),

                Block(Instruction.SET, "i = 0"),
                Block(Instruction.WHILE, "i < n"),
                    Block(Instruction.SET, "j = i + 1"),
                    Block(Instruction.WHILE, "j < n"),
                        Block(Instruction.IF, "arr[i] > arr[j]"),
                            Block(Instruction.INIT, "t = arr[i]"),
                            Block(Instruction.SET, "arr[i] = arr[j], arr[j] = t"),
                        Block(Instruction.END),
                    Block(Instruction.SET, "j += 1"),
                    Block(Instruction.END_WHILE),
                Block(Instruction.SET, "i += 1"),

                Block(Instruction.END_WHILE),
                Block(Instruction.PRINT, "arr"),
            ),
        Template.INFINITY_LOOP to arrayOf(
            Block(Instruction.INIT, "count = 0"),
            Block(Instruction.WHILE, "1"),
                Block(Instruction.PRINT, "count"),
                Block(Instruction.SET, "count += 1"),
            Block(Instruction.END_WHILE)
        ))

        fun getBlocks(template: Template): Array<Block>? {
            return templates[template]
        }
    }
}