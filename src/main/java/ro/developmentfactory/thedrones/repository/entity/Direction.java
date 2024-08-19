package ro.developmentfactory.thedrones.repository.entity;

public enum Direction {
    N, E, S, W;

    public Direction turnRight() {
        return switch (this) {
            case N -> E;
            case E -> S;
            case S -> W;
            case W -> N;
        };
    }

    public Direction turnLeft() {
        return switch (this) {
            case N -> W;
            case W -> S;
            case S -> E;
            case E -> N;
        };
    }
}
