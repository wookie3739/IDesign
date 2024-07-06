class Room {
  constructor({ startX, startY, endX, endY, color, ctx }) {
    ctx.fillStyle = color;
    ctx.fillRect(startX, startY, endX, endY);
  }
}
