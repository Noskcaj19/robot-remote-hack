export function scale(
    value: number,
    minValue: number,
    maxValue: number,
    newMin: number,
    newMax: number,
): number {
    return (
        newMin +
        ((value - minValue) * (newMax - newMin)) / (maxValue - minValue)
    )
}

export function clamp(value: number, min: number, max: number): number {
    return Math.min(Math.max(value, min), max)
}
