let dog;

function preload() {
    dog = loadJSON('dogs.json');
}

function setup() {
    createCanvas(400, 400);
    textSize(40);
}

function draw() {
    background(30);


    fill(dog.r, dog.g, dog.b);
    text(dog.name, 130, 200);

}