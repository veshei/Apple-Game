// Assignment 5
// Liang, Kevin
// kevinliang43
// Shei, Veronica
// veshei

import tester.*;                // The tester library
import javalib.worldimages.*;   // images, like RectangleImage or OverlayImages
import javalib.funworld.*;      // the abstract World class and the big-bang 
//library
import java.awt.Color;          // general colors (as triples of red,green,
// blue values) and predefined colors 
// (Red, Green, Yellow, Blue, Black, White)
import java.util.*;


// to represent a falling Apple
class Apple {

    int x;
    int y;
    int side;

    // the constructor
    Apple(int x, int y, int side) {
        this.x = x;
        this.y = y;
        this.side = side;
    }

    // methods

    // produces the image of this apple
    WorldImage appleImage() {
        return new ScaleImage(new FromFileImage("red-apple.png"), 0.5);
    }

    // moveDown() moves the Apple down by a fixed amount
    Apple moveDown() {
        return new Apple(x, y + 5, side);
    }

    // onTheGround() determines whether the apple has reached the ground.
    boolean onTheGround() {
        return this.y >= 400 - this.side;
    }

    // produces an apple as it should appear on the next tick: 
    // either fallen further down, or, if it has reached the ground,
    // it should be replaced by a new apple somewhere up on the tree.
    Apple fall() {
        if (this.onTheGround()) {
            return new Apple(
                    new Random().nextInt(400 - 2 * this.side) + this.side,
                    new Random().nextInt(50) + 50, this.side);
        }
        else {
            return this.moveDown();
        }
    }

    // returns true if this apple's x coordinates are within the two x 
    // boundaries and this apple's y coordinates are less than the given y
    // boundary
    boolean appleWithin(int xb1, int xb2, int yb1) {
        return this.x >= xb1 && this.x <= xb2 && this.y >= yb1;
    }
}

interface ILoApple {

    // applies fall() to all apples in a list of apple
    ILoApple fall();

    // returns true if this apple's x coordinates are within the two x 
    // boundaries and this apple's y coordinates are less than the given y
    // boundary
    boolean appleWithin(int xb1, int xb2, int yb1);

    // produces a new ILoApple with each apple  in the list fallen down. 
    // If the apple ended up caught in the basket, the counter of fallen apples
    // is incremented and the apple is replaced in the 
    // same manner as in the fall method designed earlier.
    ILoApple onTickHelper(int xb1, int xb2, int yb1);

    // calculates the score per tick added to total score. + 1 score for every
    // apple in a basket in the list of Apple
    int onTickScore(int xb1, int xb2, int yb1);

    // creates the scene of the list of Apples
    WorldScene appleScene(AppleGame world);
}

class MtLoApple implements ILoApple {

    // moveDown() moves each in a List of Apple down by a fixed amount
    public ILoApple fall() {
        return this;
    }

    // produces a new ILoApple with each apple  in the list fallen down. 
    // If the apple ended up caught in the basket, the counter of fallen apples
    // is incremented and the apple is replaced in the 
    // same manner as in the fall method designed earlier.
    public ILoApple onTickHelper(int xb1, int xb2, int yb1) {
        return this;
    }

    // returns true if this apple's x coordinates are within the two x 
    // boundaries and this apple's y coordinates are less than the given y
    // boundary
    public boolean appleWithin(int xb1, int xb2, int yb1) {
        return false;
    }

    // calculates the score per tick added to total score. + 1 score for every
    // apple in a basket in the list of Apple
    public int onTickScore(int xb1, int xb2, int yb1) {
        return 0;
    }

    // creates the image of the list of Apples
    public WorldScene appleScene(AppleGame world) {
        return world.getEmptyScene().placeImageXY(
                new FromFileImage("apple-tree.png"),
                world.width / 2,
                world.width / 2);
    }
}

class ConsLoApple implements ILoApple {

    Apple first;
    ILoApple rest;

    ConsLoApple(Apple first, ILoApple rest) {
        this.first = first;
        this.rest = rest;
    }

    // methods 

    // moveDown() moves each in a List of Apple down by a fixed amount
    public ILoApple fall() {
        return new ConsLoApple(this.first.fall(), this.rest.fall());
    }

    // produces a new ILoApple with each apple  in the list fallen down. 
    // If the apple ended up caught in the basket, the counter of fallen apples
    // is incremented and the apple is replaced in the 
    // same manner as in the fall method designed earlier.
    public ILoApple onTickHelper(int xb1, int xb2, int yb1) {

        int ar = this.first.side;

        if (first.appleWithin(xb1, xb2, yb1)) {
            return new ConsLoApple(new Apple(new Random().nextInt(
                    400 - 2 * ar) + ar,
                    new Random().nextInt(50) + 50, ar), 
                    this.rest.onTickHelper(xb1, xb2, yb1));
        }
        else {
            return new ConsLoApple(this.first.fall(), 
                    this.rest.onTickHelper(xb1, xb2, yb1));
        }
    }

    // calculates the score per tick added to total score. + 1 score for every
    // apple in a basket in the list of Apple
    public int onTickScore(int xb1, int xb2, int yb1) {
        if (this.first.appleWithin(xb1, xb2, yb1)) {
            return 1 + this.rest.onTickScore(xb1, xb2, yb1);
        }
        else {
            return this.rest.onTickScore(xb1, xb2, yb1);
        }
    }

    // creates the image of the list of Apples
    public WorldScene appleScene(AppleGame world) {
        return this.rest.appleScene(world).placeImageXY(
                this.first.appleImage(), 
                this.first.x, 
                this.first.y);
    }

    // returns true if this apple's x coordinates are within the two x 
    // boundaries and this apple's y coordinates are less than the given y
    // boundary
    public boolean appleWithin(int xb1, int xb2, int yb1) {
        return this.first.appleWithin(xb1, xb2, yb1) ||
                this.rest.appleWithin(xb1, xb2, yb1);
    }
}

// to represent a Basket
class Basket {

    int x;
    int y;
    int side;

    // the constructor 
    Basket(int x, int y, int side) {
        this.x = x;
        this.y = y;
        this.side = side;
    }

    // methods

    // produces the image of this basket
    WorldImage basketImage() {
        return new RectangleImage(this.side * 2, this.side * 2, 
                OutlineMode.SOLID,
                Color.ORANGE);
    }

    // moveOnKey(String ke) moves the basket depending on the given keystroke
    Basket moveOnKey(String ke) {
        int scale = 8;
        int xleft = this.x - scale - this.side;
        int xright = this.x + scale + this.side;

        if (ke.equals("left") && (xleft >= 0)) {
            return new Basket(this.x - scale, this.y, this.side);
        }
        if (ke.equals("right") && (xright <= 400)) {
            return new Basket(this.x + scale, this.y, this.side);
        }
        else {
            return this;
        }
    }

}

// to represent the Apple Orchard Game
class AppleGame extends World {

    int width = 400;
    int height = 400;
    ILoApple apples;
    Basket basket;

    int score;

    // the constructor
    AppleGame(ILoApple apples, Basket basket, int score) {
        super();
        this.apples = apples;
        this.basket = basket;
        this.score = score;
    }

    // methods 

    // caughtApple() determines whether the apple has fallen into the basket
    boolean caughtApple() {
        return this.apples.appleWithin(
                this.basket.x - this.basket.side,
                this.basket.x + this.basket.side,
                this.basket.y - this.basket.side);
    }

    // produces a new AppleGame object with the apple fallen down. 
    // If the apple ended up caught in the basket, the counter of fallen apples
    // is incremented and the apple is replaced in the 
    // same manner as in the fall method designed earlier.
    public AppleGame onTick() {
        int basketleft = this.basket.x - this.basket.side;
        int basketright = this.basket.x + this.basket.side;
        int baskettop = this.basket.y - this.basket.side;

        return new AppleGame(this.apples.onTickHelper(basketleft,
                basketright,
                baskettop), 
                this.basket, 
                this.score + this.apples.onTickScore(
                        basketleft,
                        basketright,
                        baskettop));
    }

    // allows the player to control the location of the basket on the ground.
    public AppleGame onKeyEvent(String ke) {
        return new AppleGame(this.apples, this.basket.moveOnKey(ke), score);
    }

    // produce the image of this world's apples
    public WorldScene appleScene() {
        return this.apples.appleScene(this);
    }

    // produce image of this world by adding the falling apples and basket
    public WorldScene makeScene() {
        return this.appleScene().placeImageXY(
                this.basket.basketImage(), this.basket.x, 
                this.basket.y).placeImageXY(
                        new TextImage(
                                Integer.toString(this.score), Color.BLACK)
                        , 380, 20);
    }
    // produce the last image of this world by adding text to the image
    public WorldScene lastScene(String s) {
        return this.makeScene().placeImageXY(new TextImage(s, Color.red),
                this.width / 2,
                this.height / 2); 
    }

    // Check if there player has caught 10 apples, if so, end game
    public WorldEnd worldEnds() {
        if (this.score >= 10) {
            return new WorldEnd(true, this.lastScene("Game Over"));
        }
        else {
            return new WorldEnd(false, this.makeScene());
        }
    }

}


class ExamplesAppleGame {

    Apple a1 = new Apple(200, 200, 5);
    Apple a2 = new Apple(200, 400, 5);
    Apple a3 = new Apple(200, 404, 5);
    Apple a4 = new Apple(191, 400, 5);
    Apple a5 = new Apple(209, 400, 5);
    Apple a6 = new Apple(200, 379, 5);

    Apple a7 = new Apple(150, 100, 15);
    Apple a8 = new Apple(200, 100, 15);
    Apple a9 = new Apple(250, 100, 15);

    ILoApple mt = new MtLoApple();

    ILoApple cons1 = new ConsLoApple(this.a7, this.mt);
    ILoApple cons2 = new ConsLoApple(this.a8, this.cons1);
    ILoApple cons3 = new ConsLoApple(this.a9, this.cons2);
    ILoApple cons4 = new ConsLoApple(this.a6, this.cons3);

    Basket b1 = new Basket(200, 390, 10);
    Basket b2 = new Basket(10, 390, 10);
    Basket b3 = new Basket(390, 390, 10);

    AppleGame ag1 = new AppleGame(new ConsLoApple(this.a1, this.mt), 
            this.b1, 0);
    AppleGame ag2 = new AppleGame(new ConsLoApple(this.a2, this.mt), 
            this.b1, 0);
    AppleGame ag3 = new AppleGame(new ConsLoApple(this.a3, this.mt), 
            this.b1, 0);
    AppleGame ag4 = new AppleGame(new ConsLoApple(this.a4, this.mt),
            this.b1, 0);
    AppleGame ag5 = new AppleGame(new ConsLoApple(this.a5, this.mt), 
            this.b1, 0);
    AppleGame ag6 = new AppleGame(new ConsLoApple(this.a5, this.mt),
            this.b2, 0);

    AppleGame ag7 = new AppleGame(new ConsLoApple(this.a1, this.mt), 
            this.b1, 10);
    AppleGame ag8 = new AppleGame(new ConsLoApple(this.a6, this.mt), 
            this.b1, 9);

    // test moveDown() method
    boolean testMoveDown(Tester t) {
        return t.checkExpect(this.a1.moveDown(),
                new Apple(200, 205, 5));

    }

    //test onGround() method
    boolean testOnGround(Tester t) {
        return t.checkExpect(this.a1.onTheGround(), false)
                && t.checkExpect(this.a2.onTheGround(), true)
                && t.checkExpect(this.a3.onTheGround(), true);
    }

    //test fall() method
    boolean testFall(Tester t) {
        return t.checkExpect(this.a1.fall(), new Apple(200, 205, 5))
                && t.checkRange(this.a2.fall().x, 5, 395)
                && t.checkRange(this.a2.fall().y, 50, 100)
                && t.checkRange(this.a3.fall().x, 5, 395)
                && t.checkRange(this.a3.fall().y, 50, 100)
                && t.checkExpect(this.mt.fall(), this.mt)
                && t.checkExpect(this.cons1.fall(),
                        new ConsLoApple(new Apple(150, 105, 15), this.mt));
    }

    //test moveOnKey(String ke) method
    boolean testMoveOnKey(Tester t) {
        return t.checkExpect(this.b1.moveOnKey("left"),
                new Basket(192, 390, 10))
                && t.checkExpect(this.b1.moveOnKey("right"),
                        new Basket(208, 390, 10))
                && t.checkExpect(this.b1.moveOnKey("up"),
                        new Basket(200, 390, 10))
                && t.checkExpect(this.b2.moveOnKey("left"),
                        new Basket(10, 390, 10))
                && t.checkExpect(this.b2.moveOnKey("right"),
                        new Basket(18, 390, 10))
                && t.checkExpect(this.b2.moveOnKey("up"),
                        new Basket(10, 390, 10))
                && t.checkExpect(this.b3.moveOnKey("left"),
                        new Basket(382, 390, 10))
                && t.checkExpect(this.b3.moveOnKey("right"),
                        new Basket(390, 390, 10))
                && t.checkExpect(this.b3.moveOnKey("up"),
                        new Basket(390, 390, 10));
    }

    // test caughtApple()
    boolean testCaughtApple(Tester t) {
        return t.checkExpect(this.ag1.caughtApple(), false)
                && t.checkExpect(this.ag2.caughtApple(), true)
                && t.checkExpect(this.ag3.caughtApple(), true)
                && t.checkExpect(this.ag4.caughtApple(), true)
                && t.checkExpect(this.ag5.caughtApple(), true);
    }

    // test appleWithin()
    boolean testAppleWithin(Tester t) {
        return t.checkExpect(this.a1.appleWithin(50, 70, 380), false) &&
                t.checkExpect(this.mt.appleWithin(100, 120, 380), false) &&
                t.checkExpect(this.cons1.appleWithin(190, 210, 380), false) &&
                t.checkExpect(this.cons2.appleWithin(190, 210, 380), false) &&
                t.checkExpect(this.cons4.appleWithin(190, 210, 370), true);
    }

    // test onTickHelper()
    boolean testOnTickHelper(Tester t) {
        return t.checkExpect(this.cons1.onTickHelper(200, 220, 380), 
                new ConsLoApple(new Apple(150, 105, 15), this.mt)) &&
                t.checkExpect(this.cons2.onTickHelper(180, 200, 380), 
                        new ConsLoApple(new Apple(200, 105, 15), 
                                new ConsLoApple(new Apple(150, 105, 15), 
                                        this.mt)));
    }

    // test appleScene method
    boolean testAppleScene(Tester t) {
        return t.checkExpect(this.mt.appleScene(this.ag1),
                this.ag1.getEmptyScene().placeImageXY(
                        new FromFileImage("apple-tree.png"),
                        this.ag1.width / 2,
                        this.ag1.width / 2)) &&
                t.checkExpect(this.cons1.appleScene(this.ag1),
                        this.ag1.getEmptyScene().placeImageXY(
                                new FromFileImage("apple-tree.png"),
                                this.ag1.width / 2,
                                this.ag1.width / 2).placeImageXY(
                                        this.a1.appleImage(), 
                                        this.a7.x, 
                                        this.a7.y));
    }

    //test onTickScore method
    boolean testOnTickScore(Tester t) {
        return t.checkExpect(this.cons1.onTickScore(50, 70, 380), 0) &&
                t.checkExpect(this.cons4.onTickScore(190, 210, 370), 1);
    }
    // test onTick()
    boolean testOnTick(Tester t) {
        return t.checkExpect(this.ag1.onTick(), 
                new AppleGame(new ConsLoApple(new Apple(200, 205, 5), 
                        this.mt), this.b1, 0))
                && t.checkExpect(this.ag2.onTick().basket, this.ag2.basket)
                && t.checkExpect(this.ag2.onTick().score, 1)
                && t.checkExpect(this.ag6.onTick().basket, this.ag6.basket)
                && t.checkExpect(this.ag6.onTick().score, 0);
    }

    // test onKeyEvent(String ke)
    boolean testOnKeyEvent(Tester t) {
        return t.checkExpect(this.ag1.onKeyEvent("left"),
                new AppleGame(new ConsLoApple(this.a1, this.mt), 
                        new Basket(192, 390, 10), 0))
                && t.checkExpect(this.ag1.onKeyEvent("right"),
                        new AppleGame(new ConsLoApple(this.a1, this.mt), 
                                new Basket(208, 390, 10), 0))
                && t.checkExpect(this.ag1.onKeyEvent("up"),
                        this.ag1);
    }

    // test worldEnds()
    boolean testWorldEnds(Tester t) {
        return t.checkExpect(this.ag7.worldEnds(),
                new WorldEnd(true, this.ag7.lastScene("Game Over"))) &&
                t.checkExpect(this.ag1.worldEnds(),
                        new WorldEnd(false, this.ag1.makeScene()));
    }

    // One instance of a game with 3 apples in the world
    AppleGame w = new AppleGame(
            new ConsLoApple(new Apple(30, 0, 15),
                    new ConsLoApple(new Apple(90, 100, 15),
                            new ConsLoApple(new Apple(200, 30, 15), 
                                    new MtLoApple()))),
            new Basket(200, 370, 30), 0);

    // uncomment below to run!
    // boolean runAnimation = this.w.bigBang(400, 400, 0.1);  
}
