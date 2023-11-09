package com.laggo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CellWalk {
    private int capacity;
    private int headIndex = 0;
    private BoardCell[] array;

    public CellWalk() {
        this(10);
    }

    public CellWalk(int capacity) {
        this.capacity = capacity;
        this.array = new BoardCell[capacity];
    }

    public CellWalk(List<BoardCell> cells) {
        this(cells.size());

        List<BoardCell> copy = new ArrayList<>(cells);
        while (!copy.isEmpty()) {
            this.push(copy.remove(0));
        }
    }

    private void expand() {
        this.array = Arrays.copyOf(this.array, this.capacity += this.capacity >> 1);
    }

    public void push(BoardCell cell) {
        if (headIndex == capacity) {
            this.expand();
            assert (headIndex < capacity);
        }
        array[headIndex++] = cell;
    }

    public BoardCell peek() {
        return this.array[headIndex - 1];
    }

    public BoardCell pop() {
        return this.array[--headIndex];
    }

    public boolean contains(BoardCell cell) {
        return Arrays.stream(this.array).toList().contains(cell);
    }

    public List<BoardCell> toList() {
        return Arrays.stream(this.array).toList().subList(0, this.headIndex);
    }

    public boolean isEmpty() {
        return this.headIndex > 0;
    }

    public int size() {
        return this.headIndex;
    }
}
