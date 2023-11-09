package com.laggo;

import java.util.Arrays;
import java.util.List;

public class CellStack {
    private int capacity;
    private int headIndex = 0;
    private BoardCell[] array;

    public CellStack() {
        this(10);
    }

    public CellStack(int capacity) {
        this.capacity = capacity;
        this.array = new BoardCell[capacity];
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

    public boolean isEmpty() {
        return this.headIndex > 0;
    }

    public int size() {
        return this.headIndex;
    }
}
